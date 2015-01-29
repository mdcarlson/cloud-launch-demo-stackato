<!doctype html>
<html>
<head>
<link rel="stylesheet"
	href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" />

<link rel="stylesheet" href="css/main.css" />
<style type="text/css">
table {
	padding: 0;
	border-spacing: 0;
	border-collapse: collapse;
}

* {
	font-family: "calibri";
}
</style>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script src="js/dashboard.js"></script>

<script type="text/javascript">
	var stats = {
		instances : [ {
			"state" : "RUNNING"
		}, {
			"state" : "RUNNING"
		}, {
			"state" : "RUNNING"
		}, {
			"state" : "STOPPED"
		}, {
			"state" : "CRASHED"
		}, {
			"state" : "RUNNING"
		}, {
			"state" : "RUNNING"
		}, {
			"state" : "FLAPPING"
		}, {
			"state" : "UNKNOWN"
		}, {
			"state" : "STARTING"
		} ]
	};

	var drawChart = function() {
		buildInstanceChart(stats, document.getElementById('chart'));
	};

	google.load("visualization", "1", {
		packages : [ "corechart" ]
	});
	google.setOnLoadCallback(drawChart);
</script>
</head>
<body>
	<table>
		<tr>
			<td><span class="${RUNNINGClass}"></span></td>
			<td><span class="${STARTINGClass}"></span></td>
			<td><span class="${CRASHEDClass}"></span></td>
		</tr>
		<tr>
			<td><span class="${FLAPPINGClass}"></span></td>
			<td><span class="${UNKNOWNClass}"></span></td>
			<td><span class="${STOPPEDClass}"></span></td>
		</tr>
	</table>
	<h3>Test Pie Chart</h3>
	<div id="chart"></div>
</body>
</html>