var comparison = (function () {
    return {
        compareVersions: function (data) {
            function downloadJSON(url, callback) {
                $.get(url, function (data) {
                    var json = JSON.parse(data);
                    var formattedText = JSON.stringify(json, null, 2);
                    callback(formattedText);
                });
            }

            $('#compare').mergely({
                cmsettings: {
                    readOnly: false,
                    lineWrapping: true
                },
                lhs: function (setValue) {
                    setValue('the quick red fox\njumped over the hairy dog');
                },
                rhs: function (setValue) {
                    setValue('the quick brown fox\njumped over the lazy dog');
                }
                // lhs: function(setValue) {
                //     downloadJSON($("D:/SK00434025/ATT/C3P_Docs/ConfigComparisonTask/Testing_Files/output1.txt").attr('href'), setValue);
                // },
                // rhs: function(setValue) {
                //     downloadJSON($("D:/SK00434025/ATT/C3P_Docs/ConfigComparisonTask/Testing_Files/output2.txt").attr('href'), setValue);
                // }
            });
        }

    }
})(comparison || {})
