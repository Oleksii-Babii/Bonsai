using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using BonsaiAPI.Data;
using BonsaiAPI.Models;

namespace BonsaiAPI.Controllers
{
    [Produces("application/json")]
    [Route("api/[controller]")]
    [ApiController]
    public class SpeciesController : ControllerBase
    {
        private readonly BonsaiContext _context;

        public SpeciesController(BonsaiContext context)
        {
            _context = context;
        }

        // GET: api/species
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<Species>>> GetAllSpecies()
        {
            return await _context.Species.OrderBy(s => s.Name).ToListAsync();
        }

        // GET: api/species/3
        [HttpGet("{id:int}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<Species>> GetSpecies([FromRoute] int id)
        {
            var species = await _context.Species.FindAsync(id);
            if (species == null)
            {
                return NotFound();
            }
            return Ok(species);
        }
    }
}
