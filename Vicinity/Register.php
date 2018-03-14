<?php

	$con = mysqli_connect("fdb7.biz.nf","2065980_elites","elites571632","2065980_elites");
	
	$name = $_POST("name");
	$username = $_POST("username");
	$password = $_POST("password");
	
	$statement = mysqli_prepare($con,"INSERT INTO User (name, username, password) VALUES (?,?,?) ");
	mysqli_stmt_bind_param($statement, "sss", $name, $username, $password);
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_close($statement);
	
	mysqli_close($con);
	?>
	