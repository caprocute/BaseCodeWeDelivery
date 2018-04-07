using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Description;
using DATNWebService.Models;

namespace DATNWebService.Controllers
{
    public class vehiclesController : ApiController
    {
        private DATNEntities db = new DATNEntities();

        // GET: api/vehicles
        public IQueryable<vehicle> Getvehicles()
        {
            return db.vehicles;
        }

        // GET: api/vehicles/5
        [ResponseType(typeof(vehicle))]
        public IHttpActionResult Getvehicle(string id)
        {
            vehicle vehicle = db.vehicles.Find(id);
            if (vehicle == null)
            {
                return NotFound();
            }

            return Ok(vehicle);
        }

        // PUT: api/vehicles/5
        [ResponseType(typeof(void))]
        public IHttpActionResult Putvehicle(string id, vehicle vehicle)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != vehicle.id)
            {
                return BadRequest();
            }

            db.Entry(vehicle).State = EntityState.Modified;

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!vehicleExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return StatusCode(HttpStatusCode.NoContent);
        }

        // POST: api/vehicles
        [ResponseType(typeof(vehicle))]
        public IHttpActionResult Postvehicle(vehicle vehicle)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.vehicles.Add(vehicle);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateException)
            {
                if (vehicleExists(vehicle.id))
                {
                    return Conflict();
                }
                else
                {
                    throw;
                }
            }

            return CreatedAtRoute("DefaultApi", new { id = vehicle.id }, vehicle);
        }

        // DELETE: api/vehicles/5
        [ResponseType(typeof(vehicle))]
        public IHttpActionResult Deletevehicle(string id)
        {
            vehicle vehicle = db.vehicles.Find(id);
            if (vehicle == null)
            {
                return NotFound();
            }

            db.vehicles.Remove(vehicle);
            db.SaveChanges();

            return Ok(vehicle);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool vehicleExists(string id)
        {
            return db.vehicles.Count(e => e.id == id) > 0;
        }
    }
}