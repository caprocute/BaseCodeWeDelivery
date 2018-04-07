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
    public class tripsController : ApiController
    {
        private DATNEntities db = new DATNEntities();

        // GET: api/trips
        public IQueryable<trip> Gettrips()
        {
            return db.trips;
        }

        // GET: api/trips/5
        [ResponseType(typeof(trip))]
        public IHttpActionResult Gettrip(string id)
        {
            trip trip = db.trips.Find(id);
            if (trip == null)
            {
                return NotFound();
            }

            return Ok(trip);
        }

        // PUT: api/trips/5
        [ResponseType(typeof(void))]
        public IHttpActionResult Puttrip(string id, trip trip)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != trip.id)
            {
                return BadRequest();
            }

            db.Entry(trip).State = EntityState.Modified;

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!tripExists(id))
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

        // POST: api/trips
        [ResponseType(typeof(trip))]
        public IHttpActionResult Posttrip(trip trip)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.trips.Add(trip);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateException)
            {
                if (tripExists(trip.id))
                {
                    return Conflict();
                }
                else
                {
                    throw;
                }
            }

            return CreatedAtRoute("DefaultApi", new { id = trip.id }, trip);
        }

        // DELETE: api/trips/5
        [ResponseType(typeof(trip))]
        public IHttpActionResult Deletetrip(string id)
        {
            trip trip = db.trips.Find(id);
            if (trip == null)
            {
                return NotFound();
            }

            db.trips.Remove(trip);
            db.SaveChanges();

            return Ok(trip);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool tripExists(string id)
        {
            return db.trips.Count(e => e.id == id) > 0;
        }
    }
}