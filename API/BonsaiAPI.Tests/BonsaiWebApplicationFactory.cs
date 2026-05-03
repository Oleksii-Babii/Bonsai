using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;

namespace BonsaiAPI.Tests;

public class BonsaiWebApplicationFactory : WebApplicationFactory<Program>
{
    private readonly string _dbName = $"TestDb_{Guid.NewGuid()}";

    protected override void ConfigureWebHost(IWebHostBuilder builder)
    {
        builder.UseEnvironment("Testing");
        builder.UseSetting("TestDbName", _dbName);
    }
}
