I'm using this file to record things I find about general Data Analytics as I go.

## Lead capture

In [main.js](./jcr_root/etc/designs/silversea-com/clientlib-base/js/components/main.js) you'll find a `signUpOffers` function. It looks like this function is called whenever the user fills out a form for a Contact Request, Quote Request or Brochure Request. These events obviously involve the user filling out their personal details and thus becoming a "Lead" in the system. These events are known as "Web Requests" in various places, specifically the Silversea internal CRM.

The function POSTs the data to `bin/lead.json`, which presumably is a route to the internal CRM system.

### Web Request ID (`api_indiv_id`)

This endpoint returns an property `leadResponse` which seems to represent the Web Request ID (WRID) on the CRM side. The JavaScript stores this in a cookie named `api_indiv_id`. (N.b. Whilst the name suggests this is an "Individual ID", the Adobe Analytics setup suggests it's actually the "Web Request ID").

In [c-data-layer.js](./jcr_root/etc/designs/silversea-com/clientlib-base/js/components/c-data-layer.js) there is code that reads this `api_indiv_id` cookie and stores it in `window.dataLayer[0].api_indiv_id`.

In Adobe Launch there is a custom "Data Element" called `api_indiv_id` which reads this value from `dataLayer` and exposes it as `api_indiv_id`.

Then, again in Launch, in "Rules" the main "all pages rules" maps this value to `eVar14`

### (Temporary Individual ID?) `temporaryId`

There is also something called `temporaryId` on the response which is stored in the `api_temporary_id` cookie. It might be worth looking into this further - is it the IndividualID that's been assigned?
