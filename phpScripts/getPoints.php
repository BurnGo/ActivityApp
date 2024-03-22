<?php

header('Content-Type', 'application/json');
$con = mysqli_connect("localhost", "root", "", "planetgo");
if($con){
    $q = "SELECT * FROM points_of_interests";
    $result = mysqli_query($con, $q);
    if(mysqli_num_rows($result) > 0){
        $i = 0;
        while($row = mysqli_fetch_assoc($result)){
            $data[$i] = $row;
            $i++;
        }
        echo json_encode($data, JSON_PRETTY_PRINT);
    }
}
?>