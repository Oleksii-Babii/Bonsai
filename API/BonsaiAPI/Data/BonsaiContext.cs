using Microsoft.EntityFrameworkCore;
using BonsaiAPI.Models;

namespace BonsaiAPI.Data
{
    public class BonsaiContext : DbContext
    {
        public BonsaiContext(DbContextOptions<BonsaiContext> options) : base(options)
        {
        }

        public DbSet<Species> Species { get; set; } = default!;
        public DbSet<Tree> Trees { get; set; } = default!;

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<Species>().HasData(
                new Species
                {
                    Id = 1,
                    Name = "Japanese Maple",
                    OriginCountry = "Japan",
                    Description = "Known for stunning autumn colour. Delicate leaves and elegant branching structure. Ideal for outdoor bonsai.",
                    DifficultyLevel = "Intermediate",
                    ImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/25/Acer_palmatum_001.jpg/320px-Acer_palmatum_001.jpg"
                },
                new Species
                {
                    Id = 2,
                    Name = "Chinese Elm",
                    OriginCountry = "China",
                    Description = "One of the most forgiving bonsai species. Small leaves, graceful form and reliable growth. Great for beginners.",
                    DifficultyLevel = "Beginner",
                    ImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Ulmus_parvifolia_bonsai_2.jpg/320px-Ulmus_parvifolia_bonsai_2.jpg"
                },
                new Species
                {
                    Id = 3,
                    Name = "Juniper",
                    OriginCountry = "Japan",
                    Description = "Classic bonsai choice with needle-like foliage and dramatic deadwood possibilities. Requires outdoor placement.",
                    DifficultyLevel = "Beginner",
                    ImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/Juniperus_chinensis_bonsai_1.jpg/320px-Juniperus_chinensis_bonsai_1.jpg"
                },
                new Species
                {
                    Id = 4,
                    Name = "Ficus",
                    OriginCountry = "Southeast Asia",
                    Description = "Hardy indoor bonsai with glossy leaves and strong aerial roots. Very tolerant of low-light environments.",
                    DifficultyLevel = "Beginner",
                    ImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/96/Ficus_microcarpa_bonsai_1.jpg/320px-Ficus_microcarpa_bonsai_1.jpg"
                },
                new Species
                {
                    Id = 5,
                    Name = "Azalea",
                    OriginCountry = "Japan",
                    Description = "Spectacular flowering bonsai producing vivid blooms in spring. Requires acidic soil and careful pruning after flowering.",
                    DifficultyLevel = "Expert",
                    ImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Rhododendron_bonsai_1.jpg/320px-Rhododendron_bonsai_1.jpg"
                }
            );

            modelBuilder.Entity<Tree>().HasData(
                new Tree
                {
                    Id = 1,
                    Nickname = "Old Man",
                    Age = 25,
                    Height = 45.5m,
                    LastWateredDate = new DateTime(2026, 4, 28),
                    Notes = "Repotted in spring. Needs more sunlight.",
                    ImageUrl = null,
                    SpeciesId = 1
                },
                new Tree
                {
                    Id = 2,
                    Nickname = "Little Cloud",
                    Age = 8,
                    Height = 22.0m,
                    LastWateredDate = new DateTime(2026, 4, 30),
                    Notes = "Growing well. No issues.",
                    ImageUrl = null,
                    SpeciesId = 2
                },
                new Tree
                {
                    Id = 3,
                    Nickname = "Storm",
                    Age = 15,
                    Height = 38.0m,
                    LastWateredDate = new DateTime(2026, 4, 29),
                    Notes = "Wind-swept style. Some deadwood work done in March.",
                    ImageUrl = null,
                    SpeciesId = 3
                }
            );
        }
    }
}
