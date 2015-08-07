<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html>
<body>
<table align="left" border="1">
<xsl:for-each select="root/general">
<tr>
<th  colspan="1">Code-Documentation of:<xsl:value-of select="className"/></th>
<th colspan="3" width="100"><font size="15" color="#00FF00">Valeo</font></th>
</tr>
<tr>
<th colspan="4" align="middle"><p><img src="BasicTest.png" alt="Test"></img></p></th>
</tr>
<tr>
<th colspan="4">There are: <xsl:value-of select="percentOfActiveMethods"/> % of active signals</th>
</tr>
</xsl:for-each>
<tr>
<th align="left">Signal</th>
<th align="left" width="50">line</th>
<th align="middle" width="20">1</th>
<th align="middle" width="20">0</th>
</tr>
<xsl:for-each select="root/method">
<xsl:choose>
<xsl:when test="active='0'">
<tr>
<td bgcolor="#D8F6CE"><xsl:value-of select="text"/></td>
<td width="50" bgcolor="#F5F6CE"><xsl:value-of select="line"/></td>
<td width="20"></td>
<td width="20" bgcolor="red"></td>
</tr>
</xsl:when>
<xsl:otherwise>
<tr>
<td bgcolor="#D8F6CE"><xsl:value-of select="text"/></td>
<td width="50" bgcolor="#F5F6CE"><xsl:value-of select="line"/></td>
<td width="20" bgcolor="green"></td>
<td width="20"></td>
</tr>
</xsl:otherwise>
</xsl:choose>
</xsl:for-each>
</table>
</body>
</html>
</xsl:template>
</xsl:stylesheet> 
