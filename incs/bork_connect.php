<?php

$username = "Ozafy";
$password = "SomePassword";
$database = "borknet";

$conn = mysql_connect(localhost,$username,$password) or die(mysql_error());

@mysql_select_db($database) or die( "Unable to select database");

?>