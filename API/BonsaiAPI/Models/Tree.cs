using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BonsaiAPI.Models
{
    public class Tree
    {
        [Key]
        public int Id { get; set; }

        [Required]
        [StringLength(100)]
        public string Nickname { get; set; } = string.Empty;

        [Range(0, 5000)]
        public int Age { get; set; }

        [Range(typeof(decimal), "0", "10000")]
        [Column(TypeName = "decimal(8, 2)")]
        public decimal Height { get; set; }

        public DateTime LastWateredDate { get; set; }

        public string? Notes { get; set; }

        public string? ImageUrl { get; set; }

        [ForeignKey("Species")]
        public int SpeciesId { get; set; }

        public Species? Species { get; set; }
    }
}
