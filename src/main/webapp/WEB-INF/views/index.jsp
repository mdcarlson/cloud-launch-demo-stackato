<!doctype html>
<html>
<head>
<link rel="stylesheet"
	href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" />

<link rel="stylesheet" href="css/main.css" />
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script type="text/javascript"
	src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization','version':'1','packages':['corechart','gauge']}]}""></script>
<script src="js/dashboard.js"></script>

<script type="text/javascript">
	$(dashboardClosure);
</script>
</head>
<body>
	<header>
		<div id="dashboard">
			<div id="appHealth" class="tile onethird">
				<div class="title">Application Health</div>
				<div class="container chart" id="ringChart">
					<div class="loading"><span class="fa fa-spin fa-gear"></span> Please wait...</div>
				</div>
			</div>
			<div id="instHealth" class="tile onethird">
				<div class="title">Instances</div>
				<div class="container" id="instanceStatContainer">
					<div class="loading">
						<span class="fa fa-spin fa-gear"></span> Please wait...
					</div>
				</div>
			</div>
		</div>
	</header>
	<h1>Actual Application Stuff</h1>
</body>
</html>