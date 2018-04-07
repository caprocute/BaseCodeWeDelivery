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
    public class triprequestsController : ApiController
    {
        private DATNEntities db = new DATNEntities();

        // GET: api/triprequests
        public IQueryable<triprequest> Gettriprequests()
        {
            return db.triprequests;
        }

        // GET: api/triprequests/5
        [ResponseType(typeof(triprequest))]
        public IHttpActionResult Gettriprequest(string id)
        {
            triprequest triprequest = db.triprequests.Find(id);
            if (triprequest == null)
            {
                return NotFound();
            }

            return Ok(triprequest);
        }

        // PUT: api/triprequests/5
        [ResponseType(typeof(void))]
        public IHttpActionResult Puttriprequest(string id, triprequest triprequest)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != triprequest.id)
            {
                return BadRequest();
            }

            db.Entry(triprequest).State = EntityState.Modified;

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!triprequestExists(id))
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

        // POST: api/triprequests
        [ResponseType(typeof(triprequest))]
        public IHttpActionResult Posttriprequest(triprequest triprequest)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.triprequests.Add(triprequest);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateException)
            {
                if (triprequestExists(triprequest.id))
                {
                    return Conflict();
                }
                else
                {
                    throw;
                }
            }

            return CreatedAtRoute("DefaultApi", new { id = triprequest.id }, triprequest);
        }

        // DELETE: api/triprequests/5
        [ResponseType(typeof(triprequest))]
        public IHttpActionResult Deletetriprequest(string id)
        {
            triprequest triprequest = db.triprequests.Find(id);
            if (triprequest == null)
            {
                return NotFound();
            }

            db.triprequests.Remove(triprequest);
            db.SaveChanges();

            return Ok(triprequest);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool triprequestExists(string id)
        {
            return db.triprequests.Count(e => e.id == id) > 0;
        }
    }
}