using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using BonsaiAPI.Data;
using BonsaiAPI.Models;

namespace BonsaiAPI.Controllers
{
    [Produces("application/json")]
    [Route("api/[controller]")]
    [ApiController]
    public class TreesController : ControllerBase
    {
        private readonly BonsaiContext _context;
        private readonly IWebHostEnvironment _env;

        public TreesController(BonsaiContext context, IWebHostEnvironment env)
        {
            _context = context;
            _env = env;
        }

        // GET: api/trees
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<Tree>>> GetAllTrees()
        {
            return await _context.Trees
                .Include(t => t.Species)
                .OrderBy(t => t.Nickname)
                .ToListAsync();
        }

        // GET: api/trees/1
        [HttpGet("{id:int}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<Tree>> GetTree([FromRoute] int id)
        {
            var tree = await _context.Trees.Include(t => t.Species).FirstOrDefaultAsync(t => t.Id == id);
            if (tree == null)
            {
                return NotFound();
            }
            return Ok(tree);
        }

        // GET: api/trees/search?name=old
        [HttpGet("search")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<Tree>>> SearchTrees([FromQuery] string name)
        {
            var results = await _context.Trees
                .Include(t => t.Species)
                .Where(t => t.Nickname.ToLower().Contains(name.ToLower()))
                .OrderBy(t => t.Nickname)
                .ToListAsync();

            return Ok(results);
        }

        // POST: api/trees
        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult<Tree>> PostTree([FromBody] Tree tree)
        {
            var speciesExists = await _context.Species.AnyAsync(s => s.Id == tree.SpeciesId);
            if (!speciesExists)
            {
                return BadRequest("Invalid SpeciesId.");
            }

            _context.Trees.Add(tree);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetTree), new { id = tree.Id }, tree);
        }

        public class ImageUploadDto
        {
            public IFormFile? Image { get; set; }
        }

        // POST: api/trees/1/image
        [HttpPost("{id:int}/image")]
        [Consumes("multipart/form-data")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> UploadImage([FromRoute] int id, [FromForm] ImageUploadDto dto)
        {
            var tree = await _context.Trees.FindAsync(id);
            if (tree == null) return NotFound();

            var image = dto?.Image;
            if (image == null || image.Length == 0)
                return BadRequest("No image provided.");

            var ext = Path.GetExtension(image.FileName).ToLowerInvariant();
            if (!new[] { ".jpg", ".jpeg", ".png", ".webp" }.Contains(ext))
                return BadRequest("Unsupported image format. Use jpg, png, or webp.");

            var webRoot = _env.WebRootPath ?? Path.Combine(_env.ContentRootPath, "wwwroot");
            var uploadsDir = Path.Combine(webRoot, "uploads");
            Directory.CreateDirectory(uploadsDir);

            var filename = $"tree_{id}_{DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()}{ext}";
            var filePath = Path.Combine(uploadsDir, filename);

            using (var stream = new FileStream(filePath, FileMode.Create))
            {
                await image.CopyToAsync(stream);
            }

            var baseUrl = $"{Request.Scheme}://{Request.Host}";
            tree.ImageUrl = $"{baseUrl}/uploads/{filename}";
            await _context.SaveChangesAsync();

            return Ok(new { imageUrl = tree.ImageUrl });
        }

        // PUT: api/trees/1
        [HttpPut("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> PutTree([FromRoute] int id, [FromBody] Tree tree)
        {
            if (id != tree.Id)
            {
                return BadRequest("ID mismatch.");
            }

            var existing = await _context.Trees.FindAsync(id);
            if (existing == null)
            {
                return NotFound();
            }

            existing.Nickname = tree.Nickname;
            existing.Age = tree.Age;
            existing.Height = tree.Height;
            existing.LastWateredDate = tree.LastWateredDate;
            existing.Notes = tree.Notes;
            existing.ImageUrl = tree.ImageUrl;
            existing.ImageData = tree.ImageData ?? existing.ImageData;
            existing.SpeciesId = tree.SpeciesId;

            await _context.SaveChangesAsync();
            return NoContent();
        }

        // DELETE: api/trees/1
        [HttpDelete("{id:int}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> DeleteTree([FromRoute] int id)
        {
            var tree = await _context.Trees.FindAsync(id);
            if (tree == null)
            {
                return NotFound();
            }

            _context.Trees.Remove(tree);
            await _context.SaveChangesAsync();
            return NoContent();
        }
    }
}
