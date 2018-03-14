<?php
    $con = mysqli_connect("fdb7.biz.nf", "2065980_elites", "elites571632", "2065980_elites");
    
    $username = $_POST["username"];
    $title = $_POST["title"];
	$message = $_POST["message"];
	$gps = $_POST["gps"];
    
	
    $statement = mysqli_prepare($con, "UPDATE user SET gps = '$gps'  WHERE username = '$username' ");
    mysqli_stmt_bind_param($statement, "ssss", $username, $title, $message, $gps);
    mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $username, $title, $message, $gps);
    
    $response = array();
    $response["success"] = false;  
    
    while(mysqli_stmt_fetch($statement))
    {
        $response["success"] = true;  
        $response["username"] = $username;
        $response["title"] = $title;
        $response["message"] = $message;
        $response["gps"] =  $gps;
    }
    
    echo json_encode($response);
?>