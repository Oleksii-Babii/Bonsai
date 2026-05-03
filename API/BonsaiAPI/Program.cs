using Microsoft.AspNetCore.Http.Features;
using Microsoft.EntityFrameworkCore;
using System.Text.Json.Serialization;
using BonsaiAPI.Data;
using System.Runtime.InteropServices;

var builder = WebApplication.CreateBuilder(args);
builder.WebHost.UseWebRoot("wwwroot");

var useInMemory = builder.Environment.IsEnvironment("Testing")
    || RuntimeInformation.IsOSPlatform(OSPlatform.OSX);

var testDbName = builder.Configuration["TestDbName"] ?? "BonsaiLocalDev";

builder.Services.AddDbContext<BonsaiContext>(options =>
{
    if (useInMemory)
        options.UseInMemoryDatabase(testDbName);
    else
        options.UseSqlServer(builder.Configuration.GetConnectionString("BonsaiContext")
            ?? throw new InvalidOperationException("Connection string 'BonsaiContext' not found."));
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
    if (useInMemory)
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
