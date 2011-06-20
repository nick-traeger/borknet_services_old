<?php
error_reporting(0);
$debug=false;
if($debug)
{
 ini_set('display_errors','1');
 ini_set('display_startup_errors','1');
 error_reporting (E_ALL);
}
set_time_limit(15);
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>The BorkNet IRC Network - W Example</title>
</head>
<body>
<br><b class="underline">W Example:</b><br>
<?php
//the following bit of code uses the included java program to connect to W which will make interaction a lot easyer:
$command="Q say #dev-com php connection using java";
$output = shell_exec("java W 127.0.0.1 4444 'passwordgoeshere!' ".escapeshellarg($command));
echo '<pre>'.$output.'</pre>';
######################################################################################################################
//the following code connects to W using php, this allows for more customisation but also requires more php knowledge:
$server_host = "127.0.0.1";
$server_port = 4444;
$password = "passwordgoeshere!";
$command = "Q say #dev-com php connection using php sockets";
$server = array();
echo "<pre>Connecting to server...\n\r";
$server['SOCKET'] = @fsockopen($server_host, $server_port, $errno, $errstr, 2);
if($server['SOCKET'])
{
 $cmd="PASS ".$password."\n";
 fwrite($server['SOCKET'], $cmd, strlen($cmd));
 $cmd=$command."\n";
 fwrite($server['SOCKET'], $cmd, strlen($cmd));
 $cmd=".\n\r";
 fwrite($server['SOCKET'], $cmd, strlen($cmd));
 flush();
}
socket_close($server['SOCKET']);
echo "Connection Closed.</pre>";
?>
</body>
</html>