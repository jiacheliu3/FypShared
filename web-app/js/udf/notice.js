function notifyMe() {
	$.notify({
		// options
		message : 'Job is done.'
	}, {
		// settings
		type : 'success'
	});
}
function errorNotice(job){

	$.notify({
		// options
		message : 'An error occurred processing the '+job
	}, {
		// settings
		type : 'danger'
	});

}
function testRestartReminder() {
	$.notify(
					{
						// options
						title : 'Crawler done.',
						message : '<a href="/CodeBigBroSub/environment/studyRestartTest">Here we go again</a>'
					}, {
						// settings
						type : 'success'
					});
}
function restartReminder() {
	$.notify(
					{
						// options
						title : 'Crawler done.',
						message : '<a href="/CodeBigBroSub/environment/restartEntrance">Here we go again</a>'
					}, {
						// settings
						type : 'info'
					});
}
function sendNotice(m){
	$.notify(
			{
				// options
				message : m+' job is done. You can go to the corresponding page to check out the result.'
			}, {
				// settings
				type : 'success'
			});
	
}