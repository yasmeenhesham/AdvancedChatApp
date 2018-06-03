'use strict'

const functions = require('firebase-functions');

const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref(`/Notifications/{user_id}/{notification_id}/`).onWrite((change, context) =>{
  const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;

  console.log('We have a notification to send to ', user_id);

	  if(!change.after.val()){
			 console.log("A Notification has been deleted from the database", notification_id);
	  }
  
  const fromUser = admin.database().ref(`/Notifications/${user_id}/${notification_id}`).once('value');
  return fromUser.then(fromUserResult => {
	  const from_user_id = fromUserResult.val().from;
	  console.log("You have new Notification from ", from_user_id);
	  const userQuery = admin.database().ref(`/Users/${from_user_id}/name`).once('value');
      const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');
	  const userStatus = admin.database().ref(`/Users/${from_user_id}/status`).once('value');
	  const userImage = admin.database().ref(`/Users/${from_user_id}/image`).once('value');
	  return Promise.all([userQuery,deviceToken,userStatus,userImage]).then(result => {
			const userName = result[0].val();
			const token_id = result[1].val();
			const status = result[2].val();
			const image = result[3].val();
		    console.log("token", token_id);

			const payload ={
				
					  notification: {
						title : "Friend request",
						body : `${userName} has sent you request`,
						icon : "default",
						click_action : "com.example.yasmeen.advancedchatapp_Target_Notification"
					  },
					  data : {
					  from_user_id : from_user_id,
					  from_user_name : userName,
					  from_user_status : status,
					  from_user_image : image
					  }
				};

				return admin.messaging().sendToDevice(token_id, payload).then(response =>{
					console.log('This was the notofication Feature');
				});
	  });

	  
	  
  });

  

});
 
