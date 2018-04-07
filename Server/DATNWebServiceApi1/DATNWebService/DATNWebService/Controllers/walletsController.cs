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
    public class walletsController : ApiController
    {
        private DATNEntities db = new DATNEntities();

        // GET: api/wallets
        public IQueryable<wallet> Getwallets()
        {
            return db.wallets;
        }

        // GET: api/wallets/5
        [ResponseType(typeof(wallet))]
        public IHttpActionResult Getwallet(string id)
        {
            wallet wallet = db.wallets.Find(id);
            if (wallet == null)
            {
                return NotFound();
            }

            return Ok(wallet);
        }

        // PUT: api/wallets/5
        [ResponseType(typeof(void))]
        public IHttpActionResult Putwallet(string id, wallet wallet)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (id != wallet.id)
            {
                return BadRequest();
            }

            db.Entry(wallet).State = EntityState.Modified;

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!walletExists(id))
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

        // POST: api/wallets
        [ResponseType(typeof(wallet))]
        public IHttpActionResult Postwallet(wallet wallet)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.wallets.Add(wallet);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateException)
            {
                if (walletExists(wallet.id))
                {
                    return Conflict();
                }
                else
                {
                    throw;
                }
            }

            return CreatedAtRoute("DefaultApi", new { id = wallet.id }, wallet);
        }

        // DELETE: api/wallets/5
        [ResponseType(typeof(wallet))]
        public IHttpActionResult Deletewallet(string id)
        {
            wallet wallet = db.wallets.Find(id);
            if (wallet == null)
            {
                return NotFound();
            }

            db.wallets.Remove(wallet);
            db.SaveChanges();

            return Ok(wallet);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool walletExists(string id)
        {
            return db.wallets.Count(e => e.id == id) > 0;
        }
    }
}