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
    public class accountsController : ApiController
    {
        private DATNEntities db = new DATNEntities();

        // GET: api/accounts
        public IQueryable<account> Getaccounts()
        {
            return db.accounts;
        }

        // GET: api/accounts/5
        [ResponseType(typeof(account))]
        public IHttpActionResult Getaccount(string id)
        {
            account account = db.accounts.Find(id);
            if (account == null)
            {
                return NotFound();
            }

            return Ok(account);
        }

        // PUT: api/accounts/5
        [ResponseType(typeof(void))]
        public IHttpActionResult Putaccount(string id, account account)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != account.id)
            {
                return BadRequest();
            }

            db.Entry(account).State = EntityState.Modified;

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!accountExists(id))
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

        // POST: api/accounts
        [ResponseType(typeof(account))]
        public IHttpActionResult Postaccount(account account)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.accounts.Add(account);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateException)
            {
                if (accountExists(account.id))
                {
                    return Conflict();
                }
                else
                {
                    throw;
                }
            }

            return CreatedAtRoute("DefaultApi", new { id = account.id }, account);
        }

        // DELETE: api/accounts/5
        [ResponseType(typeof(account))]
        public IHttpActionResult Deleteaccount(string id)
        {
            account account = db.accounts.Find(id);
            if (account == null)
            {
                return NotFound();
            }

            db.accounts.Remove(account);
            db.SaveChanges();

            return Ok(account);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool accountExists(string id)
        {
            return db.accounts.Count(e => e.id == id) > 0;
        }
    }
}