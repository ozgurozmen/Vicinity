<?php
    $con = mysqli_connect("fdb7.biz.nf", "2065980_elites", "elites571632", "2065980_elites");
    
	if (mysqli_connect_errno()) 
	{
    printf("Connect failed: %s\n", mysqli_connect_error());
    exit();
	}
	
    $gps = $_POST["gps"];
 
    $sql = "SELECT * FROM messages WHERE gps = '$gps'");
    
    //mysqli_stmt_store_result($statement);
    //mysqli_stmt_bind_result($statement, $username, $title, $message, $gps);
    
	$result = mysqli_query($con, $sql);
	
    //$response = array();
    //response["success"] = false;

	$data = array();
    
    while($row = mysqli_fetch_assoc($result) )
	{
		$data[] = $row;
        //$response["success"] = true;  
        //$response["username"] = $username;
		//$response["title"] = $title;
		//$response["message"] = $message;
        //$response["gps"] =  $gps;
    }
    
    echo json_encode($data);
?>
