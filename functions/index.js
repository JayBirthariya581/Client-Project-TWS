const functions = require("firebase-functions");


const Razorpay = require("razorpay");
const crypto = require('crypto');
const admin = require('firebase-admin');
admin.initializeApp();

var key_id = "rzp_test_lZQYZSVQyrdHCs";
var key_secret = "MGqybOTnAmsVySuBkDEJlasQ";

var instance = new Razorpay({ key_id: 'rzp_test_lZQYZSVQyrdHCs', key_secret: 'MGqybOTnAmsVySuBkDEJlasQ' })


 
   exports.createOrderID = functions.https.onCall((data_val, context) => {

    return new Promise((resolve, reject) => {

     instance.orders.create({
      amount: data_val.amt,
      currency: "INR",
      receipt: data_val.receipt,
       },function (err,data)
       {

        if (err) {
          // something went wrong, send error back to caller
          reject(new functions.https.HttpsError('unknown', 'An unexpected error', err));
          return;
        }

        resolve(data);

        // res.json({ order_id: data.id, currency: data.currency, amount : data.amount });

        })
  
      
    });
  
     });

  
    



  exports.verifySignature = functions.https.onCall((data_sign, context_sign) => {

    
    return new Promise((resolve, reject) => {
    
    var signature = data_sign.razorpay_signature;
    var razorpayOrderId =	data_sign.razorpay_order_id;
    var razorpayPaymentId = data_sign.razorpay_payment_id; 0.


    const hmac = crypto.createHmac('sha256', key_secret);

    hmac.update(razorpayOrderId + "|" + razorpayPaymentId);
  
    let generatedSignature = hmac.digest('hex');

    let isSignatureValid = generatedSignature == signature;

    if(isSignatureValid==true)
    {
      resolve(generatedSignature);
    }
    
    else{
      reject("Not Authorized");
      return;
    }

    });


  });




  exports.refundExecution = functions.https.onCall((data_refund, context_re) => {

    return new Promise((resolve, reject) => {
    
      instance.payments.refund(data_refund.paymentID).then((data) => { 

        //It return values in JSON Format --- resolve({refund_id: data.id, payment_id: data.payment_id, amount : data.amount })}) or resolve(data)
        resolve(data.id);  // we need refund id so... return refund_id(string)
        
      }).catch((error) => {
        reject(new functions.https.HttpsError('unknown', 'An unexpected error', error));
          return;
      });
    });

  });