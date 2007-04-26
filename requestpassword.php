<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>BorkNet Channel Service Password Reset Page Thing.</title>
</head>
<body>
<?php

if ($_GET['user'] == '' && $_GET['activation'] == '') {
  echo "<br><b class=\"underline\">ERROR!</b>";
  echo "<br>&nbsp;<br>No username/code given!<br>&nbsp;<br>&nbsp;";
} else {
  $user = $_GET['user'];
  $code = $_GET['activation'];

  include "incs/bork_connect.php";

  # get the total rows in the database
  $query = "SELECT * FROM q_pwrequest WHERE user = '$user' LIMIT 1";
  $result = mysql_query($query, $conn);
  $rows = mysql_num_rows($result);
  if ($rows > 0) {
    while ($item = mysql_fetch_assoc($result)) {
      if($item['code'] == $code) {
        $pass = $item['pass'];
        $hash = md5($pass);
        $query = "UPDATE auths SET pass = '$hash' WHERE authnick = '$user'";
        mysql_query($query);
        echo "<br><b class=\"underline\">Done!</b>";
        echo "<br>&nbsp;";
        echo "<br>Your new password is $pass";
        echo "<br>&nbsp;";
        echo "<br>You can AUTH using one of the following commands:";
        echo "<br>/msg q@cserve.borknet.org AUTH $user $pass";
        echo "<br>&nbsp;";
        echo "<br>You can use the newpass command to change your password:";
        echo "<br>/msg Q newpass $pass newpassword newpassword";
        echo "<br>&nbsp;<br>&nbsp;";
        $query = "DELETE FROM q_pwrequest WHERE user = '$user'";
        mysql_query($query);
        mysql_close($conn);
      } else {
        echo "<br><b class=\"underline\">ERROR!</b>";
        echo "<br>&nbsp;<br>Incorrect username/code given!<br>&nbsp;<br>&nbsp;";
      }
    }
  } else {
    echo "<br><b class=\"underline\">ERROR!</b>";
    echo "<br>&nbsp;<br>Incorrect username/code given!<br>&nbsp;<br>&nbsp;";
  }
}
?>
<b><br>&nbsp;Copyright &copy; 2005 by <a href="http://www.borknet.org">BorkNet IRC Network</a> - All rights reserved.</b>
<br>&nbsp;
<br>
</body>
</html>