using System.Net;
using System.Net.Http.Json;
using BonsaiAPI.Models;

namespace BonsaiAPI.Tests;

public class TreesControllerTests : IClassFixture<BonsaiWebApplicationFactory>
{
    private readonly HttpClient _client;

    public TreesControllerTests(BonsaiWebApplicationFactory factory)
    {
        _client = factory.CreateClient();
    }

    // ── GET all ──────────────────────────────────────────────────────────────

    [Fact]
    public async Task GetAllTrees_Returns200()
    {
        var response = await _client.GetAsync("/api/trees");
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
    }

    [Fact]
    public async Task GetAllTrees_ReturnsThreeSeededTrees()
    {
        var trees = await _client.GetFromJsonAsync<List<Tree>>("/api/trees");
        Assert.NotNull(trees);
        Assert.Equal(3, trees.Count);
    }

    [Fact]
    public async Task GetAllTrees_ReturnsOrderedByNickname()
    {
        var trees = await _client.GetFromJsonAsync<List<Tree>>("/api/trees");
        Assert.NotNull(trees);
        var names = trees.Select(t => t.Nickname).ToList();
        Assert.Equal(names.OrderBy(n => n).ToList(), names);
    }

    // ── GET by id ─────────────────────────────────────────────────────────────

    [Fact]
    public async Task GetTree_Returns200_ForExistingId()
    {
        var response = await _client.GetAsync("/api/trees/1");
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
    }

    [Fact]
    public async Task GetTree_ReturnsCorrectTree()
    {
        var tree = await _client.GetFromJsonAsync<Tree>("/api/trees/1");
        Assert.NotNull(tree);
        Assert.Equal(1, tree.Id);
        Assert.False(string.IsNullOrEmpty(tree.Nickname));
    }

    [Fact]
    public async Task GetTree_ReturnsSpeciesNavigation()
    {
        var tree = await _client.GetFromJsonAsync<Tree>("/api/trees/1");
        Assert.NotNull(tree);
        Assert.NotNull(tree.Species);
    }

    [Fact]
    public async Task GetTree_Returns404_ForNonExistingId()
    {
        var response = await _client.GetAsync("/api/trees/999");
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    [Fact]
    public async Task SearchTrees_Returns200()
    {
        var response = await _client.GetAsync("/api/trees/search?name=old");
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
    }

    [Fact]
    public async Task SearchTrees_ReturnsMatchingResults()
    {
        var trees = await _client.GetFromJsonAsync<List<Tree>>("/api/trees/search?name=old");
        Assert.NotNull(trees);
        Assert.Contains(trees, t => t.Nickname.Contains("Old", StringComparison.OrdinalIgnoreCase));
    }

    [Fact]
    public async Task SearchTrees_IsCaseInsensitive()
    {
        var lower = await _client.GetFromJsonAsync<List<Tree>>("/api/trees/search?name=storm");
        var upper = await _client.GetFromJsonAsync<List<Tree>>("/api/trees/search?name=STORM");
        Assert.NotNull(lower);
        Assert.NotNull(upper);
        Assert.Equal(lower.Count, upper.Count);
    }

    [Fact]
    public async Task SearchTrees_ReturnsEmpty_WhenNoMatch()
    {
        var trees = await _client.GetFromJsonAsync<List<Tree>>("/api/trees/search?name=xyznotfound");
        Assert.NotNull(trees);
        Assert.Empty(trees);
    }

    // ── POST ──────────────────────────────────────────────────────────────────

    [Fact]
    public async Task PostTree_Returns201_WithValidData()
    {
        var newTree = new Tree
        {
            Nickname = "Test Pine",
            Age = 10,
            Height = 30m,
            LastWateredDate = DateTime.UtcNow,
            Notes = "Test notes",
            SpeciesId = 1
        };

        var response = await _client.PostAsJsonAsync("/api/trees", newTree);
        Assert.Equal(HttpStatusCode.Created, response.StatusCode);
    }

    [Fact]
    public async Task PostTree_ReturnsCreatedTree()
    {
        var newTree = new Tree
        {
            Nickname = "My Maple",
            Age = 5,
            Height = 20m,
            LastWateredDate = DateTime.UtcNow,
            SpeciesId = 2
        };

        var response = await _client.PostAsJsonAsync("/api/trees", newTree);
        var created = await response.Content.ReadFromJsonAsync<Tree>();
        Assert.NotNull(created);
        Assert.True(created.Id > 0);
        Assert.Equal("My Maple", created.Nickname);
    }

    [Fact]
    public async Task PostTree_Returns400_WithInvalidSpeciesId()
    {
        var newTree = new Tree
        {
            Nickname = "Ghost Tree",
            Age = 1,
            Height = 10m,
            LastWateredDate = DateTime.UtcNow,
            SpeciesId = 999
        };

        var response = await _client.PostAsJsonAsync("/api/trees", newTree);
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }

    // ── PUT ───────────────────────────────────────────────────────────────────

    [Fact]
    public async Task PutTree_Returns204_WithValidData()
    {
        var tree = await _client.GetFromJsonAsync<Tree>("/api/trees/1");
        Assert.NotNull(tree);
        tree.Nickname = "Updated Nickname";

        var response = await _client.PutAsJsonAsync("/api/trees/1", tree);
        Assert.Equal(HttpStatusCode.NoContent, response.StatusCode);
    }

    [Fact]
    public async Task PutTree_PersistsChanges()
    {
        var tree = await _client.GetFromJsonAsync<Tree>("/api/trees/2");
        Assert.NotNull(tree);
        tree.Age = 99;

        await _client.PutAsJsonAsync("/api/trees/2", tree);

        var updated = await _client.GetFromJsonAsync<Tree>("/api/trees/2");
        Assert.NotNull(updated);
        Assert.Equal(99, updated.Age);
    }

    [Fact]
    public async Task PutTree_Returns400_WhenIdMismatch()
    {
        var tree = await _client.GetFromJsonAsync<Tree>("/api/trees/1");
        Assert.NotNull(tree);
        tree.Id = 99;

        var response = await _client.PutAsJsonAsync("/api/trees/1", tree);
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }

    [Fact]
    public async Task PutTree_Returns404_WhenTreeNotExists()
    {
        var tree = new Tree
        {
            Id = 999,
            Nickname = "Ghost",
            Age = 1,
            Height = 5m,
            LastWateredDate = DateTime.UtcNow,
            SpeciesId = 1
        };

        var response = await _client.PutAsJsonAsync("/api/trees/999", tree);
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    [Fact]
    public async Task DeleteTree_Returns204_WhenExists()
    {
        var newTree = new Tree
        {
            Nickname = "To Be Deleted",
            Age = 1,
            Height = 5m,
            LastWateredDate = DateTime.UtcNow,
            SpeciesId = 1
        };
        var created = await (await _client.PostAsJsonAsync("/api/trees", newTree))
            .Content.ReadFromJsonAsync<Tree>();
        Assert.NotNull(created);

        var response = await _client.DeleteAsync($"/api/trees/{created.Id}");
        Assert.Equal(HttpStatusCode.NoContent, response.StatusCode);
    }

    [Fact]
    public async Task DeleteTree_Returns404_WhenNotExists()
    {
        var response = await _client.DeleteAsync("/api/trees/999");
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    [Fact]
    public async Task DeleteTree_RemovesTreeFromList()
    {
        var newTree = new Tree
        {
            Nickname = "Temporary",
            Age = 2,
            Height = 10m,
            LastWateredDate = DateTime.UtcNow,
            SpeciesId = 1
        };
        var created = await (await _client.PostAsJsonAsync("/api/trees", newTree))
            .Content.ReadFromJsonAsync<Tree>();
        Assert.NotNull(created);

        await _client.DeleteAsync($"/api/trees/{created.Id}");

        var response = await _client.GetAsync($"/api/trees/{created.Id}");
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }
}
