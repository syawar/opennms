<!--

//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2003 Feb 07: Fixed URLEncoder issues.
// 2002 Nov 26: Fixed breadcrumbs issue.
// 
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//

-->

<%@page language="java" contentType="text/html" session="true"
	import="org.opennms.netmgt.config.*,java.util.*,org.opennms.netmgt.config.users.*"%>

<html>
<head>
<title>New User Info | User Admin | OpenNMS Web Console</title>
<base HREF="<%=org.opennms.web.Util.calculateUrlBase( request )%>" />
<link rel="stylesheet" type="text/css" href="css/styles.css" />
</head>

<body marginwidth="0" marginheight="0" LEFTMARGIN="0" RIGHTMARGIN="0"
	TOPMARGIN="0">

<% String breadcrumb1 = "<a href='admin/index.jsp'>Admin</a>"; %>
<% String breadcrumb2 = "<a href='admin/userGroupView/index.jsp'>Users and Groups</a>"; %>
<% String breadcrumb3 = "<a href='admin/userGroupView/users/list.jsp'>User List</a>"; %>
<% String breadcrumb4 = "New User"; %>
<jsp:include page="/includes/header.jsp" flush="false">
	<jsp:param name="title" value="New User" />
	<jsp:param name="breadcrumb" value="<%=breadcrumb1%>" />
	<jsp:param name="breadcrumb" value="<%=breadcrumb2%>" />
	<jsp:param name="breadcrumb" value="<%=breadcrumb3%>" />
	<jsp:param name="breadcrumb" value="<%=breadcrumb4%>" />
</jsp:include>

<script language="JavaScript">
  function validateFormInput() 
  {
    var id = new String(document.newUserForm.userID.value);
    if (id.toLowerCase()=="admin")
    {
        alert("The user ID '" + document.newUserForm.userID.value + "' cannot be used. It may be confused with the administration user ID 'admin'.");
        return;
    }
    
    if (document.newUserForm.pass1.value == document.newUserForm.pass2.value) 
    {
      document.newUserForm.action="admin/userGroupView/users/addNewUser";
      document.newUserForm.submit();
    } 
    else
    {
      alert("The two password fields do not match!");
      document.newUserForm.pass1.value = "";
      document.newUserForm.pass2.value = "";
    }
  }    
  function cancelUser()
  {
      document.newUserForm.action="admin/userGroupView/users/list.jsp";
      document.newUserForm.submit();
  }

</script>

<br>

<form id="newUserForm" method="post" name="newUserForm">
<table width="100%" border="0" cellspacing="0" cellpadding="2">
	<tr>
		<td>&nbsp;</td>

		<td><%if (request.getParameter("action").equals("redo")) { %>
		<h3>The user <%=request.getParameter("userID")%> already exists.
		Please type in a different user id.</h3>
		<%} else { %>
		<h3>Please enter a user id and password below.</h3>
		<%}%>
		<table>
			<tr>
				<td width="10%"><label id="userIDLabel" for="userID">User ID:</label></td>
				<td width="100%"><input id="userID" type="text" name="userID"></td>
			</tr>

			<tr>
				<td width="10%"><label id="pass1Label" for="password1">Password:</label></td>
				<td width="100%"><input id="pass1" type="password" name="pass1"></td>
			</tr>

			<tr>
				<td width="10%"><label id="pass2Label" for="password2">Confirm Password:</label></td>
				<td width="100%"><input id="pass2" type="password" name="pass2"></td>
			</tr>

			<tr>
				<td><input id="doOK" type="button" value="OK" onClick="validateFormInput()"></td>
				<td><input id="doCancel" type="button" value="Cancel" onClick="cancelUser()"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>

<br> <jsp:include page="/includes/footer.jsp" flush="false" />
</body>
</html>
