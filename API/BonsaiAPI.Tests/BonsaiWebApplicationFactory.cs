using BonsaiAPI.Data;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;

namespace BonsaiAPI.Tests;

public class BonsaiWebApplicationFactory : WebApplicationFactory<Program>
{
    private readonly string _dbName = $"TestDb_{Guid.NewGuid()}";

    protected override void ConfigureWebHost(IWebHostBuilder builder)
    {
        builder.ConfigureServices(services =>
        {
            var descriptor = services.SingleOrDefault(
                d => d.ServiceType == typeof(DbContextOptions<BonsaiContext>));
            if (descriptor != null) services.Remove(descriptor);

            services.AddDbContext<BonsaiContext>(options =>
                options.UseInMemoryDatabase(_dbName));
        });
    }
}
