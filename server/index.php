<?php
$data=[];
foreach($_GET as $key=>$value) {
	$data[$key]=$value;
}
foreach($_POST as $key=>$value) {
	$data[$key]=$value;
}
$header=get_headers();
if(isset($data["device"]) && !isse($header["device"])) {
	$header["device"]=$data["device"];
}
if(isset($header["device"])) {
	$values=["content"=>$header["device"]];
	if($db->count("device", $values) == 0) {
		$db->insert("device", $values);
	}
	$device=$db->select("device", $values);
}
if(!isset($device) || $device == null || count($device) == 0) {
	exit("error access!");
}
if(isset($data["type"], $data["content"])) {
	$type=$data["type"];
	$content=$data["content"];
	$datetime=$data["datetime"];
	$data=$datetime . " : " . $type." : " . print_r(json_decode($content)) . "\n";
	file_put_contents("backup.txt", $data, FILE_APPEND);
	$values=[
		"type"=>$type,
		"content"=>$content,
		"datetime"=>$datetime,
	];
	$db->insert("log", $values);
	print "ok";
}
else {
	exit("error!");
}
