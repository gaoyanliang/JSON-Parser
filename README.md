# JSON-Parser
JSON解析器

JSON(JavaScript Object Notation, JS 对象简谱) 是一种轻量级的数据交换格式。易于人阅读和编写。同时也易于机器解析和生成。采用完全独立于语言的文本格式，但是也使用了类似于C语言家族的习惯（包括C, C++, C#, Java, JavaScript, Perl, Python等）。这些特性使JSON成为理想的数据交换语言。
 
 JSON与JS的区别以及和XML的区别具体请参考[百度百科](https://baike.baidu.com/item/JSON/2462549?fr=aladdin)
 
 **JSON有两种结构：**
 
 第一种：对象
>“名称/值”对的集合不同的语言中，它被理解为对象（object），纪录（record），结构（struct），字典（dictionary），哈希表（hash table），有键列表（keyed list），或者关联数组 （associative array）。

>对象是一个无序的“‘名称/值’对”集合。一个对象以“{”（左括号）开始，“}”（右括号）结束。每个“名称”后跟一个“:”（冒号）；“‘名称/值’ 对”之间使用“,”（逗号）分隔。

```json
{"姓名": "张三", "年龄": "18"}
```

第二种：数组
>值的有序列表（An ordered list of values）。在大部分语言中，它被理解为数组（array）。

>数组是值（value）的有序集合。一个数组以“[”（左中括号）开始，“]”（右中括号）结束。值之间使用“,”（逗号）分隔。

>值（value）可以是双引号括起来的字符串（string）、数值(number)、true、false、 null、对象（object）或者数组（array）。这些结构可以嵌套。

```json
[
	{ 
	"姓名": "张三",			 
	"年龄":"18"	 
	},
			 
	{		 
	"姓名": "里斯",			 
	"年龄":"19"	

	}
]
```

解析的具体过程：[传送门](https://gyl-coder.top/JSONParser)
