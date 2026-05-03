using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace BonsaiAPI.Migrations
{
    /// <inheritdoc />
    public partial class AddTreeImageData : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "ImageData",
                table: "Trees",
                type: "nvarchar(max)",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "ImageData",
                table: "Trees");
        }
    }
}
