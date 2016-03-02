<style>
form {
	display: table;
}

p {
	display: table-row;
}

label {
	display: table-cell;
}

input {
	display: table-cell;
	
}
input.form-control{
	height:50px;
	width:400px;
}
/* Set fonts */
#inputForm{
	font-size:120%;
}
</style>
<script>
	function checkFields() {
		console.log("validating form before submit..");
		
		var i = document.forms["inputForm"]["wId"].value;
		var u = document.forms["inputForm"]["wUrl"].value;
		if ((n == null || n == "") && (i == null || i == "")
				&& (u == null || u == "")) {
			document.getElementById("formNotice").textContent = "You have left all fields as blank!";
			document.getElementById("formNotice").style.color = "red";
			return false;
		}
		return true;
	}
</script>
<div id="inputForm" style="margin: 50px;">
	
	
	<p>Please provide the user's weibo id (digits) or simply the user's homepage url for the crawler to start.</p>
	<br />
	<g:form controller="framework" action="crawlBeforeStart"
		onsubmit="return checkFields()" name="inputForm" >
		

			<div class="form-group">
				<label>Weibo ID: </label> 
				<input class="form-control"
					placeholder="Enter the user id" name="wId">
			</div>
			<div class="form-group">
				<label>Weibo Url: </label> 
				<input class="form-control"
					placeholder="Enter the user page url" name="wUrl">
			</div>
			
		
		<button type="submit" class="btn btn-primary"><b>Start</b></button>
	</g:form>
	<br />
	<p id="formNotice"></p>
	<br />
	<p style="font-style: italic; width: 60%;">Notes: Username is
		preferred over ID over user URL. Also, if the information don't match
		each other, the search will be based on the upper one.</p>
</div>