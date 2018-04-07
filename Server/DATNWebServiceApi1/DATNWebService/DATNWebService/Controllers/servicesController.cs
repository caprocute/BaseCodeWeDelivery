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
    public class servicesController : ApiController
    {
        private DATNEntities db = new DATNEntities();

        // GET: api/services
        public IQueryable<service> Getservices()
        {
            return db.services;
        }

        // GET: api/services/5
        [ResponseType(typeof(service))]
        public IHttpActionResult Getservice(string id)
        {
            service service = db.services.Find(id);
            if (service == null)
            {
                return NotFound();
            }

            return Ok(service);
        }

        // PUT: api/services/5
        [ResponseType(typeof(void))]
        public IHttpActionResult Putservice(string id, service service)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != service.id)
            {
                return BadRequest();
            }

            db.Entry(service).State = EntityState.Modified;

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!serviceExists(id))
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

        // POST: api/services
        [ResponseType(typeof(service))]
        public IHttpActionResult Postservice(service service)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.services.Add(service);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateException)
            {
                if (serviceExists(service.id))
                {
                    return Conflict();
                }
                else
                {
                    throw;
                }
            }

            return CreatedAtRoute("DefaultApi", new { id = service.id }, service);
        }

        // DELETE: api/services/5
        [ResponseType(typeof(service))]
        public IHttpActionResult Deleteservice(string id)
        {
            service service = db.services.Find(id);
            if (service == null)
            {
                return NotFound();
            }

            db.services.Remove(service);
            db.SaveChanges();

            return Ok(service);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool serviceExists(string id)
        {
            return db.services.Count(e => e.id == id) > 0;
        }
    }
}