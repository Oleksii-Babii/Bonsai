using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace BonsaiAPI.Migrations
{
    /// <inheritdoc />
    public partial class InitialCreate : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Species",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Name = table.Column<string>(type: "nvarchar(100)", maxLength: 100, nullable: false),
                    OriginCountry = table.Column<string>(type: "nvarchar(100)", maxLength: 100, nullable: false),
                    Description = table.Column<string>(type: "nvarchar(max)", nullable: false),
                    DifficultyLevel = table.Column<string>(type: "nvarchar(20)", maxLength: 20, nullable: false),
                    ImageUrl = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Species", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Trees",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Nickname = table.Column<string>(type: "nvarchar(100)", maxLength: 100, nullable: false),
                    Age = table.Column<int>(type: "int", nullable: false),
                    Height = table.Column<decimal>(type: "decimal(8,2)", nullable: false),
                    LastWateredDate = table.Column<DateTime>(type: "datetime2", nullable: false),
                    Notes = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    ImageUrl = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    SpeciesId = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Trees", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Trees_Species_SpeciesId",
                        column: x => x.SpeciesId,
                        principalTable: "Species",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.InsertData(
                table: "Species",
                columns: new[] { "Id", "Description", "DifficultyLevel", "ImageUrl", "Name", "OriginCountry" },
                values: new object[,]
                {
                    { 1, "Known for stunning autumn colour. Delicate leaves and elegant branching structure. Ideal for outdoor bonsai.", "Intermediate", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/25/Acer_palmatum_001.jpg/320px-Acer_palmatum_001.jpg", "Japanese Maple", "Japan" },
                    { 2, "One of the most forgiving bonsai species. Small leaves, graceful form and reliable growth. Great for beginners.", "Beginner", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Ulmus_parvifolia_bonsai_2.jpg/320px-Ulmus_parvifolia_bonsai_2.jpg", "Chinese Elm", "China" },
                    { 3, "Classic bonsai choice with needle-like foliage and dramatic deadwood possibilities. Requires outdoor placement.", "Beginner", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/Juniperus_chinensis_bonsai_1.jpg/320px-Juniperus_chinensis_bonsai_1.jpg", "Juniper", "Japan" },
                    { 4, "Hardy indoor bonsai with glossy leaves and strong aerial roots. Very tolerant of low-light environments.", "Beginner", "https://upload.wikimedia.org/wikipedia/commons/thumb/9/96/Ficus_microcarpa_bonsai_1.jpg/320px-Ficus_microcarpa_bonsai_1.jpg", "Ficus", "Southeast Asia" },
                    { 5, "Spectacular flowering bonsai producing vivid blooms in spring. Requires acidic soil and careful pruning after flowering.", "Expert", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Rhododendron_bonsai_1.jpg/320px-Rhododendron_bonsai_1.jpg", "Azalea", "Japan" }
                });

            migrationBuilder.InsertData(
                table: "Trees",
                columns: new[] { "Id", "Age", "Height", "ImageUrl", "LastWateredDate", "Nickname", "Notes", "SpeciesId" },
                values: new object[,]
                {
                    { 1, 25, 45.5m, null, new DateTime(2026, 4, 28, 0, 0, 0, 0, DateTimeKind.Unspecified), "Old Man", "Repotted in spring. Needs more sunlight.", 1 },
                    { 2, 8, 22.0m, null, new DateTime(2026, 4, 30, 0, 0, 0, 0, DateTimeKind.Unspecified), "Little Cloud", "Growing well. No issues.", 2 },
                    { 3, 15, 38.0m, null, new DateTime(2026, 4, 29, 0, 0, 0, 0, DateTimeKind.Unspecified), "Storm", "Wind-swept style. Some deadwood work done in March.", 3 }
                });

            migrationBuilder.CreateIndex(
                name: "IX_Trees_SpeciesId",
                table: "Trees",
                column: "SpeciesId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Trees");

            migrationBuilder.DropTable(
                name: "Species");
        }
    }
}
