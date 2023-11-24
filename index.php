<?php

# action, student id, and semester must be set
if(isset($_REQUEST['action'])&&isset($_REQUEST['id'])&&isset($_REQUEST['semester'])){

    $dbc = connect_db(); # get DB connection here
    
    # remove all white spaces from both sides
    $action = trim($_REQUEST['action']);
    $std_id = trim($_REQUEST['id']);
    $semester = trim($_REQUEST['semester']);
    
    # remove all special characters
    $action = mysqli_real_escape_string($dbc, $action);
    $std_id = mysqli_real_escape_string($dbc, $std_id);
    $semester = mysqli_real_escape_string($dbc, $semester);
    
    # remove all characters except numbers
    $std_id = preg_replace('/[^0-9]/', '', $std_id);     # correct 2019360123
    $semester = preg_replace('/[^0-9]/', '', $semester); # correct 20222
    
    $response = array("msg" => "Something went wrong");
    
    if(strlen($std_id)==10 && strlen($semester)==5){
    	
    	$table_name = "_YOUR_TABLE_NAME_HERE";
    	
        if($action=="backup"){
            # remove all white spaces from both sides
            $e_key = trim($_REQUEST['key']);
            $e_event = trim($_REQUEST['event']);
            # remove all special characters
            $e_key = mysqli_real_escape_string($dbc, $e_key);
            $e_event = mysqli_real_escape_string($dbc, $e_event);
            

            $sql = "INSERT INTO (std_id, semester, e_key, e_value) VALUES('$std_id', '$semester', '$e_key', '$e_event')";
        
            if(mysqli_query($dbc, $sql)) {
                $response["msg"] = "New event created successfully";
            } else {
                $error = mysqli_error($dbc);
                if(strpos($error, "Duplicate entry") !== false){
                    $sql = "UPDATE $table_name SET e_value='$e_event' WHERE std_id='$std_id' AND semester='$semester' AND e_key='$e_key'";
                    if(mysqli_query($dbc, $sql)) {
                        $response["msg"] = "Event updated successfully";
                    } else {
                        $error = mysqli_error($dbc);
                        $response["msg"] = "Error: " . $sql . "<br>$error";
                    }
                } else {
                    $response["msg"] = "Error: " . $sql . "<br>$error";
                }
            }
        }
        else if($action=="restore"){
            $sql = "SELECT e_key, e_value FROM $table_name WHERE std_id='$std_id' AND semester='$semester'";
            $r = mysqli_query($dbc, $sql);
            if($r && mysqli_num_rows($r)>0){
                $events = array();
                while(($row=mysqli_fetch_assoc($r))!=0){
                    $events[] = array("key"=>$row['e_key'], "value"=>$row['e_value']);
                }
                $response = array("msg" => "OK", "events"=>$events);
            }
        }
    }
    $json = json_encode($response,JSON_HEX_TAG | JSON_HEX_APOS | JSON_HEX_QUOT | JSON_HEX_AMP | JSON_UNESCAPED_UNICODE);
    echo $json;
} else {
    echo "You are not allowed to access.";
}

?>