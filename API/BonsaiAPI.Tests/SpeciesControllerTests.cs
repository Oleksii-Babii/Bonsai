using System.Net;
using System.Net.Http.Json;
using BonsaiAPI.Models;

namespace BonsaiAPI.Tests;

public class SpeciesControllerTests : IClassFixture<BonsaiWebApplicationFactory>
{
    private readonly HttpClient _client;

    public SpeciesControllerTests(BonsaiWebApplicationFactory factory)
    {
        _client = factory.CreateClient();
    }

    [Fact]
    public async Task GetAllSpecies_Returns200()
    {
        var response = await _client.GetAsync("/api/species");
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
    }

    [Fact]
    public async Task GetAllSpecies_ReturnsFiveSeededSpecies()
    {
        var species = await _client.GetFromJsonAsync<List<Species>>("/api/species");
        Assert.NotNull(species);
        Assert.Equal(5, species.Count);
    }

    [Fact]
    public async Task GetAllSpecies_ReturnsOrderedByName()
    {
        var species = await _client.GetFromJsonAsync<List<Species>>("/api/species");
        Assert.NotNull(species);
        var names = species.Select(s => s.Name).ToList();
        Assert.Equal(names.OrderBy(n => n).ToList(), names);
    }

    [Fact]
    public async Task GetSpecies_Returns200_ForExistingId()
    {
        var response = await _client.GetAsync("/api/species/1");
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
    }

    [Fact]
    public async Task GetSpecies_ReturnsCorrectSpecies()
    {
        var species = await _client.GetFromJsonAsync<Species>("/api/species/1");
        Assert.NotNull(species);
        Assert.Equal(1, species.Id);
        Assert.False(string.IsNullOrEmpty(species.Name));
    }

    [Fact]
    public async Task GetSpecies_Returns404_ForNonExistingId()
    {
        var response = await _client.GetAsync("/api/species/999");
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }
}
