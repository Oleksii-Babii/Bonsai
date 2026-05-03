using Microsoft.AspNetCore.Http.Features;
using Microsoft.EntityFrameworkCore;
using System.Text.Json.Serialization;
using BonsaiAPI.Data;
using System.Runtime.InteropServices;

var builder = WebApplication.CreateBuilder(args);
builder.WebHost.UseWebRoot("wwwroot");

var useInMemoryDatabase = RuntimeInformation.IsOSPlatform(OSPlatform.OSX);

builder.Services.AddDbContext<BonsaiContext>(options =>
{
    if (useInMemoryDatabase)
    {
        options.UseInMemoryDatabase("BonsaiLocalDev");
    }
    else
    {
        options.UseSqlServer(builder.Configuration.GetConnectionString("BonsaiContext")
            ?? throw new InvalidOperationException("Connection string 'BonsaiContext' not found."));
    }
});

builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles;
    });
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
        policy.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
});

// Allow image uploads up to 10 MB
builder.Services.Configure<FormOptions>(options =>
{
    options.MultipartBodyLengthLimit = 10 * 1024 * 1024;
});

var app = builder.Build();

var webRootPath = app.Environment.WebRootPath ?? Path.Combine(app.Environment.ContentRootPath, "wwwroot");
Directory.CreateDirectory(webRootPath);
Directory.CreateDirectory(Path.Combine(webRootPath, "uploads"));

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<BonsaiContext>();
    if (db.Database.IsInMemory())
        db.Database.EnsureCreated();
    else
        db.Database.Migrate();
}

app.UseSwagger();
app.UseSwaggerUI();

app.UseStaticFiles();

if (!app.Environment.IsDevelopment())
{
    app.UseHttpsRedirection();
}

app.UseCors();

app.UseAuthorization();

app.MapControllers();

app.Run();

public partial class Program {}
