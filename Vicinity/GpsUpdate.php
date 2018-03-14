<?php
    $con = mysqli_connect("fdb7.biz.nf", "2065980_elites", "elites571632", "2065980_elites");
    
    $username = $_POST["username"];
    $gps = $_POST["gps"];
	
    $statement = mysqli_prepare($con, "UPDATE users SET gps = ?  WHERE username = ?");
    mysqli_stmt_bind_param($statement, "s", $username);
    mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID, $name, $username, $password, $gps);
    
    $response = array();
    $response["success"] = false;  
    
    while(mysqli_stmt_fetch($statement))
    {
        $response["success"] = true;  
        $response["name"] = $name;
        $response["username"] = $username;
        $response["password"] = $password;
        $response["gps"] =  $null;
    }
    
    echo json_encode($response);
?>