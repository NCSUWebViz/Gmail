var R;
var importances = 4;
var timelines = 5;
var importanceList = [ "Not Important", "Less Important", "Important",
		"Very Important" ];
var timeList = [ "Today", "Yesterday", "This week", "Later this month", "Older" ];

var timestampList = [];
var region = {};

window.onload = function() {
	$
			.ajax({
				type : "POST",
				url : "/GmailViz/intervals",
				success : function(msg) {
					R = Raphael("paper", window.innerWidth, window.innerHeight);
					msg = eval(' (' + msg + ') ');
					var originX = 200;
					var originY = 150;
					var length = 150;
					var height = 100;
					var textY = originY + (height / 2);
					var textX = originX / 2;

					for ( var i = 0; i < importances; i++) {
						var t = R.text(textX, textY, importanceList[i]);
						t.attr({
							"font-size" : 16,
							"font-family" : "Arial, Helvetica, sans-serif"
						});
						textY = textY + height;
					}
					textY = originY + (4 * height) + 10;

					for ( var i = 0; i < msg.Intervals.length; i++) {
						timestampList[i] = msg.Intervals[i].timestampString;
					}

					for ( var i = 0; i < 5; i++) {
						textX = originX + (length * i);
						var t = R
								.text(textX + (length / 2), textY, timeList[i]);
						t.attr({
							"font-size" : 16,
							"font-family" : "Arial, Helvetica, sans-serif"
						});
					}

					var attr = {
						fill : "#E4EDC2",
						stroke : "#666",
						"stroke-width" : 1,
						"stroke-linejoin" : "round"
					};
					var originX = 200;
					var originY = 150;

					var instanceOriginX;
					var instanceOriginY;

					var length = 150;
					var height = 100;
					var path = "";

					for ( var imp = 0; imp < importances; imp++) {
						for ( var timeline = 0; timeline < timelines; timeline++) {
							instanceOriginX = originX + timeline * length;
							instanceOriginY = originY + imp * height;
							path = "M" + instanceOriginX + ","
									+ instanceOriginY + "," + "L"
									+ (instanceOriginX + length) + ","
									+ instanceOriginY + "," + "L"
									+ (instanceOriginX + length) + ","
									+ (instanceOriginY + height) + "," + "L"
									+ (instanceOriginX) + ","
									+ (instanceOriginY + height) + "," + "L"
									+ (instanceOriginX) + "," + instanceOriginY;
							region[imp + "" + timeline] = R.path(path).attr(
									attr);
						}
					}
					var current = null;
					for ( var state in region) {
						region[state].color = Raphael.getColor();
						(function(st, state) {
							st[0].style.cursor = "pointer";
							st[0].onmouseover = function() {
								current = state;
								region[current].animate({
									fill : "#E4EDC2;",
									stroke : "#666"
								}, 500);
								st.animate({
									fill : st.color,
									stroke : "#ccc"
								}, 500);
								st.toFront();
							};
							st[0].onmousedown = function() {
								window.location = location.protocol
										+ '//'
										+ location.host
										+ "/GmailViz/mainstat.jsp?importance="
										+ importanceList[state.substr(0, 1)]
										+ "&from="
										+ timestampList[state.substr(1, 1)]
										+ "&to="
										+ timestampList[parseInt(state.substr(
												1, 1)) + 1] + "&range="
										+ timeList[state.substr(1, 1)];
							};
							st[0].onmouseout = function() {
								st.animate({
									fill : "#E4EDC2",
									stroke : "#666"
								}, 500);
								st.toFront();
							};
						})(region[state], state);
					}
				}
			});
};