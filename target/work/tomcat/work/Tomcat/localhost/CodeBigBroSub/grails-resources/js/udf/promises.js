function getMapData() {
	function reject(response){
		console.log("Sth went wrong in ajax call");
		console.log(response);
	}
	function resolve(response){
		console.log("Ajax call has returned");
	}
	var a = new Promise(function(resolve, reject) {
		$.ajax({
			type : 'GET',
			dataType : 'json',
			url : "/CodeBigBroSub/environment/asynchroMainlandData",
			success : function(response) {
				console.log("got mainland data ");
				console.log(response);
			},
			error : function(response) {
				reject(response);
			}
		});
	});
	var b = new Promise(function(resolve, reject) {
		$.ajax({
			type : 'GET',
			dataType : 'json',
			url : "/CodeBigBroSub/environment/asynchroTaiwanData",
			success : function(response) {
				console.log("got taiwan data ");
				console.log(response);
			},
			error : function(response) {
				reject(response);
			}
		});
	});
	var c = new Promise(function(resolve, reject) {
		$.ajax({
			type : 'GET',
			dataType : 'json',
			url :"/CodeBigBroSub/environment/asynchroMacauData",
			success : function(response) {
				console.log("got macau data ");
				console.log(response);
			},
			error : function(response) {
				reject(response);
			}
		});
	});
	// This will run once all async operations have successfully finished
	Promise.all([ a, b, c ]).then(function(data) {
		// everything successful, handle data here
		console.log("got the data from chained promises");
		console.log(data);
	}, function(data) {
		// something failed, handle error here
		console.log("Error in ajax promises.");
		//logoutError(data);
	});
}