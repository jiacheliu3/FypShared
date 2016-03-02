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

input.form-control {
	height: 50px;
	width: 400px;
}
/* Set fonts */
#inputForm {
	font-size: 120%;
}
</style>
<script>
	function checkFields() {
		console.log("validating form before submit..");
		var n = document.forms["inputForm"]["wName"].value;
		
		if (n == null || n == "") {
			document.getElementById("formNotice").textContent = "You have left the field blank!";
			document.getElementById("formNotice").style.color = "red";
			return false;
		}
		return true;
	}
</script>
<div id="inputForm" style="margin: 50px;">

	<g:form controller="environment" action="beforeStart"
		onsubmit="return checkFields()" name="inputForm">

		<div class="form-group">
			<label>Weibo Username: </label> <input class="form-control"
				placeholder="Enter the username" name="wName">
		</div>

		<button type="submit" class="btn btn-primary"><b>Start</b></button>
		<!-- 
		<g:actionSubmit name="Start manhunt" value="beforeStart"
			class="btn btn-primary btn-circle"/>
		 -->
	</g:form>
	<div>
		<br/>
		<p>
	   If you are not sure about the user name, this button will direct you to a page where you provide more detailed information for out crawler to start from. 
	   </p>
	</div>
	<g:link controller="framework" action="detailedIndex"> 
	   <input type="button" value="I know the user's id or homepage url, but not the exact name." class="btn btn-outline btn-default"  /> 
	   
	</g:link>
	<br />
	<p id="formNotice"></p>
	<br />
	<p style="font-style: italic; width: 60%;">Notes: If you are simply not sure about the exact user name or that name has changed, please go to the more detailed search page by clicking the button above to provide exact weibo user id for searching to start.</p>
</div>