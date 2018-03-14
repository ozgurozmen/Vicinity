<?php

	$con = mysqli_connect("fdb7.biz.nf","2065980_elites","elites571632","2065980_elites");

	$username = $_POST("username");
	$password = $_POST("password");
	
	$statement = mysqli_prepare($con, "SELECT * FROM User WHERE username = ? AND password = ? ");
	mysqli_stmt_bind_param($statement, "ss",$username, $password);
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $id, $name, $username, $password);
	
	$user = array();
	
	while (mysqli_stmt_fetch($statement)){
		$user[name] = $name;
		$user[username] = $username;
		$user[password] = $password;
	
	}
	
	echo json_encode($user);
	
	mysqli_stmt_close($statement);
	
	mysqli_close($con);
	?>