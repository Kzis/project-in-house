searchUser{
	select US.USERID , US.USEREMAIL
	,ST.TITLEDESCRTHA
	,EM.FIRSTNAMETHA ,EM.LASTNAMETHA ,EM.NICKNAME ,EM.CARDID
	,SP.POSITIONNAME ,SP.POSITIONID
	from [OC].SEC_USER US
	inner join [OC].EMP_MEMBER EM on US.EMPLOYEEID = EM.EMPLOYEEID
	inner join [OC].STS_POSITION SP on EM.POSITIONID = SP.POSITIONID
	left JOIN STS_TITLENAME ST ON EM.TITLEID = ST.TITLEID 
	WHERE EM.EMPSTATUS <> 'I'/* I  ออกไปแล้ว*/
	AND EM.DELETED = 'F' /*ยังไม่โดนลบ*/
	ORDER BY SP.POSITIONID , EM.FIRSTNAMETHA
}