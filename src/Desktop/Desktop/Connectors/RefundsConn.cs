﻿/*
 * MIT License
 *
 * Copyright (c) 2020 The Mauzo Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Desktop.Connectors
{
    class RefundsConn
    {
        private string MauzoUrl = Settings.Default.MauzoServer + "/api/refunds";
        private string token = LoginConn.Token;

        public List<Refund> List
        {
            get
            {
                // Iniciamos la conexión.
                Uri baseUrl = new Uri(MauzoUrl + "/");
                IRestClient client = new RestClient(baseUrl);
                IRestRequest request = new RestRequest(Method.GET);

                // Agregamos la autorización de token en el header.
                request.AddHeader("Authorization", token);

                // Ejecutamos la petición.
                IRestResponse response = client.Execute(request);

                // Inicializamos la lista de devoluciones
                List<Refund> refunds = null;

                // Procesamos el objeto de la devolución
                if (response.IsSuccessful)
                    refunds = JsonConvert.Deserialize<List<Refund>>(response.Content);
                else
                    LoginConn.CalculateException(response, "No se ha encontrado la devolución");

                // Devolvemos el objeto
                return refunds;
            }
        }

        public void Add(Refund refund)
        {
            // Iniciamos la conexión.
            Uri baseUrl = new Uri(MauzoUrl + "/");
            IRestClient client = new RestClient(baseUrl);
            IRestRequest request = new RestRequest(Method.POST);

            // Agregamos la autorización de token en el header.
            request.AddHeader("Authorization", token);

            // Convertimos la devolución a json y lo incorporamos a la petición.
            string jsonRequest = JsonConvert.SerializeObject(new
            {
                
                dateRefund = refund.DateRefund,
                userId = refund.UserId,
                saleId = refund.SaleId

            });

            // Se añade el json al cuerpo de la petición.
            request.AddJsonBody(jsonRequest);

            // Ejecutamos la petición.
            IRestResponse response = client.Execute(request);

            // Procesamos la respuesta de la petición.
            if (!response.IsSuccessful)
                LoginConn.CalculateException(response, "No se ha encontrado la devolución");
        }

        public Refund Get(int id)
        {
            // Iniciamos la conexión.
            Uri baseUrl = new Uri(MauzoUrl + "/" + id);
            IRestClient client = new RestClient(baseUrl);
            IRestRequest request = new RestRequest(Method.GET);

            // Agregamos la autorización de token en el header.
            request.AddHeader("Authorization", token);

            // Ejecutamos la petición.
            IRestResponse response = client.Execute(request);

            // Inicializamos la devolución
            Refund refund = null;

            // Procesamos el objeto de la devolución
            if (response.IsSuccessful)
                refund = JsonConvert.DeserializeObject<Refund>(response.Content);
            else
                LoginConn.CalculateException(response, "No se ha encontrado la devolución.");
            
            // Devolvemos el objeto
            return refund;
        }

        public void Modify(Refund refund)
        {
            // Iniciamos la conexión.
            Uri baseUrl = new Uri(MauzoUrl + "/" + refund);
            IRestClient client = new RestClient(baseUrl);
            IRestRequest request = new RestRequest(Method.PUT);

            // Agregamos la autorización de token en el header.
            request.AddHeader("Authorization", token);

            // Convertimos la devolución a json y lo incorporamos a la petición.
            string jsonRequest = JsonConvert.SerializeObject(new
            {
                dateRefund = refund.DateRefund,
                userId = refund.UserId,
                saleId = refund.SaleId
            });

            // Se añade el json al cuerpo de la petición.
            request.AddJsonBody(jsonRequest);

            // Ejecutamos la petición.
            IRestResponse response = client.Execute(request);

            // Procesamos la respuesta de la petición.
            if (!response.IsSuccessful)
                LoginConn.CalculateException(response, "No se ha encontrado la devolución");
        }

        public void Delete(Refund refund)
        {
            // Iniciamos la conexión.
            Uri baseUrl = new Uri(MauzoUrl + "/" + refund.Id);
            IRestClient client = new RestClient(baseUrl);
            IRestRequest request = new RestRequest(Method.DELETE);

            // Agregamos la autorización de token en el header.
            request.AddHeader("Authorization", token);

            // Ejecutamos la petición.
            IRestResponse response = client.Execute(request);

            // Procesamos la respuesta de la petición.
            if (!response.IsSuccessful)
                LoginConn.CalculateException(response, "No se ha encontrado la devolución");
        }

    }
}
