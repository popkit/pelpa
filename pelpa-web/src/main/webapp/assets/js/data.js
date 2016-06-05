function build(labels, label, data) {
    return {
        labels: labels,
        datasets: [
            {
                label: label,
                fill: false,
                lineTension: 0.1,
                backgroundColor: "rgba(75,192,192,0.4)",
                borderColor: "rgba(75,192,192,1)",
                borderCapStyle: 'butt',
                borderDash: [],
                borderDashOffset: 0.0,
                borderJoinStyle: 'miter',
                pointBorderColor: "rgba(75,192,192,1)",
                pointBackgroundColor: "#fff",
                pointBorderWidth: 1,
                pointHoverRadius: 5,
                pointHoverBackgroundColor: "rgba(75,192,192,1)",
                pointHoverBorderColor: "rgba(220,220,220,1)",
                pointHoverBorderWidth: 2,
                pointRadius: 1,
                pointHitRadius: 10,
                pointStyle: 'star',
                data: data
            }
        ]
    };
}

function buildFromObj(obj) {
    return build(obj.labels, obj.label, obj.data);
}

function draw() {
    var ctxMonth = document.getElementById("myChartMonth");
    var ctxToday = document.getElementById("myChartToday");
    $.post("/elpa/log/ajaxmonthss.json", {
    }, function (dataRes) {
        if (dataRes.hasOwnProperty("month")) {
            $('#myChartMonthDiv').removeClass('hidden');
            var myLineChartMonth = new Chart(ctxMonth, {
                type: 'line',
                data: buildFromObj(dataRes.month)
            });
        } else {
            console.log("no month data");
        }

        if (dataRes.hasOwnProperty("today")) {
            $('#myChartTodayDiv').removeClass('hidden');
            var myLineChartToday = new Chart(ctxToday, {
                type: 'line',
                data: buildFromObj(dataRes.today)
            });
        } else {
            console.log("no today data");
        }
    });
}

draw();