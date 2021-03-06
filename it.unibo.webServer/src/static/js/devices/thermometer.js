
var slider_temp = document.getElementById("temp");
var output_temp = document.getElementById("value_temp");

slider_temp.oninput = function() {
    output_temp.innerHTML = this.value;
    thermometer_socket.send(JSON.stringify({
        'data': this.value,
    }));
};


const thermometer_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/ws/thermometer/'
);

thermometer_socket.onmessage = function (e) {
    const data = JSON.parse(e.data);
    console.log(data)
    document.querySelector('#msg_temp').innerHTML = (data.data)
    document.querySelector("#temp").value = parseInt(data.data)
    document.querySelector("#value_temp").innerHTML = (data.data)
}

