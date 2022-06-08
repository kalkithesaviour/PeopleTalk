<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>PeopleTalk</title>
<link href="css/bootstrap.min.css" rel="stylesheet" />
<link href="css/custom.css" rel="stylesheet" />
<link href="datetimepicker/css/datetimepicker.min.css" rel="stylesheet" />
<script src="https://www.google.com/recaptcha/api.js" async defer></script>
</head>
<body data-spy="scroll" data-target="#my-navbar">
	<nav class="navbar navbar-inverse navbar-fixed-top" id="my-navbar">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-collapse">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a href="/" class="navbar-brand">PeopleTalk</a>
			</div>
		</div>
	</nav>
	<div class="container">
		<section>
			<div class="carousel slide" id="screenshot-carousel" data-ride="carousel">
				<ol class="carousel-indicators">
					<li data-target="#screenshot-carousel" data-slide-to="0" class="active"></li>
					<li data-target="#screenshot-carousel" data-slide-to="1"></li>
					<li data-target="#screenshot-carousel" data-slide-to="2"></li>
				</ol>
				<div class="carousel-inner">
					<div class="item active">
						<img src="img/pt1.jpg" alt="Text of the image" height="10" width="1200">
						<div class="carousel-caption">
							<h1></h1>
							<p></p>
						</div>
					</div>
					<div class="item">
						<img src="img/pt2.jpg" alt="Text of the image" height="10" width="1200">
						<div class="carousel-caption">
							<h1></h1>
							<p></p>
						</div>
					</div>
					<div class="item">
						<img src="img/pt1.jpg" alt="Text of the image" height="10" width="1200">
						<div class="carousel-caption">
							<h1></h1>
							<p></p>
						</div>
					</div>
				</div>
				<a href="#screenshot-carousel" class="left carousel-control" data-slide="prev">
					<span class="glyphicon glyphicon-chevron-left"></span>
				</a>
				<a href="#screenshot-carousel" class="right carousel-control" data-slide="next">
					<span class="glyphicon glyphicon-chevron-right"></span>
				</a>
			</div>
		</section>
		<hr>
	</div>
	<div class="container">
		<section>
			<div class="row">
				<div class="col-lg-6">
					<p>${addResult}</p>
					<div class="panel panel-default">
						<div class="panel-heading text-center">
							<h3>Registration</h3>
						</div>
						<div class="panel-body">
							<form action="addUserGoogle" method="post" data-toggle="validator" enctype="multipart/form-data" class="form-horizontal">
								<div class="form-group">
									<label for="email" class="col-lg-3 control-label">Email:</label>
									<div class="col-lg-9">
										<input type="email" name="email" class="form-control" id="email" placeholder="Enter your email" required />
									</div>
								</div>
								<div class="form-group">
									<label for="name" class="col-lg-3 control-label">Name:</label>
									<div class="col-lg-9">
										<input type="text" name="name" class="form-control" pattern="^[_A-Z a-z]{1,}$" id="name" placeholder="Enter your name" required />
									</div>
								</div>
								<div class="form-group">
									<label for="phone" class="col-lg-3 control-label">Phone:</label>
									<div class="col-lg-9">
										<input type="text" name='phone' class="form-control" pattern="^[_0-9]{1,}$" maxlength="10" minlength="10" id="phone" placeholder="Enter your phone" required />
									</div>
								</div>
								<div class="form-group">
									<label for="gender" class="col-lg-3 control-label">Gender:</label>
									<div class="col-lg-9">
										<input type="radio" id="gender" name="gender" value="male" checked />Male <input type="radio" id="gender" name="gender" value="female" />Female
									</div>
								</div>
								<div class="form-group">
									<label for="dob" class="col-lg-3 control-label">Date of Birth:</label>
									<div class="col-lg-9">
										<input type="text" name="dob" class="form-control" id="dob" placeholder="dd/mm/yyyy" required />
									</div>
								</div>
								<div class="form-group">
									<label for="state" class="col-lg-3 control-label">State:</label>
									<div class="col-lg-9">
										<select name="state" class="form-control" id="listBox" onchange='select_district(this.value)'></select>
									</div>
								</div>
								<div class="form-group">
									<label for="city" class="col-lg-3 control-label">City:</label>
									<div class="col-lg-9">
										<select name="city" class="form-control" id='secondlist'></select>
									</div>
								</div>
								<div class="form-group">
									<label for="area" class="col-lg-3 control-label">Area:</label>
									<div class="col-lg-9">
										<input type="text" name="area" class="form-control" id="area" placeholder="Enter your Area" required />
									</div>
								</div>
								<div class="form-group">
									<label for="photo" class="col-lg-3 control-label">Photo:</label>
									<div class="col-lg-9">
										<input type="file" name="photo" class="form-control" id="photo" required />
									</div>
								</div>
								<div class="form-group">
									<div class="col-lg-10 col-lg-offset-3">
										<sec:csrfInput />
										<button type="submit" class="btn btn-primary">Register</button>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>
	<hr>
	<div class="navbar navbar-inverse navbar-fixed-bottom">
		<div class="container">
			<div class="navbar-text pull-left">
				<p>Designed and Developed by Vishal Singh Adhikari</p>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="js/jquery-2.2.2.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<script type="text/javascript" src="js/script.js"></script>
	<script type="text/javascript" src="js/validator.js"></script>
	<script type="text/javaScript" src='js/state.js'></script>
	<script type="text/javascript" src="datetimepicker/js/moment.min.js"></script>
	<script type="text/javascript" src="datetimepicker/js/datetimepicker.min.js"></script>
	<script type="text/javascript">
		$(function() {
			$('#dob').datetimepicker({
				format : 'DD/MM/YYYY',
				maxDate : new Date()
			});
		});
	</script>
</body>
</html>