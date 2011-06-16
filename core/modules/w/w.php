<?php
error_reporting(0);
$debug=false;
if($debug)
{
 ini_set('display_errors','1');
 ini_set('display_startup_errors','1');
 error_reporting (E_ALL);
}
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd"><html>
<head>
<title>The BorkNet IRC Network - W Example</title>
</head>
<body>
<br><b class="underline">W Example:</b>
<br>&nbsp;
<?php
set_time_limit(15);
$server_host = "127.0.0.1";
$server_port = 4444;
$server = array();
echo "<pre>Connecting to server...\n\r";
$server['SOCKET'] = @fsockopen($server_host, $server_port, $errno, $errstr, 2);
if($server['SOCKET'])
{
 $cmd="PASS passwordgoeshere!\n";
 fwrite($server['SOCKET'], $cmd, strlen($cmd));
 $cmd="Q say #dev-com Hello there!\n";
 fwrite($server['SOCKET'], $cmd, strlen($cmd));
 $cmd=".\n\r";
 fwrite($server['SOCKET'], $cmd, strlen($cmd));
 flush();
}
socket_close($server['SOCKET']);
echo "Connection Closed.</pre>";
?>