using System.ComponentModel.DataAnnotations;

namespace BonsaiAPI.Models
{
    public class Species
    {
        [Key]
        public int Id { get; set; }

        [Required]
        [StringLength(100)]
        public string Name { get; set; } = string.Empty;

        [Required]
        [StringLength(100)]
        public string OriginCountry { get; set; } = string.Empty;

        [Required]
        public string Description { get; set; } = string.Empty;

        [Required]
        [StringLength(20)]
        public string DifficultyLevel { get; set; } = string.Empty;

        public string? ImageUrl { get; set; }

        public ICollection<Tree> Trees { get; set; } = new List<Tree>();
    }
}
