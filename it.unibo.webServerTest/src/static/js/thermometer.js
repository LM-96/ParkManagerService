
var slider_temp = document.getElementById("temp");
var output_temp = document.getElementById("value_temp");
output_temp.innerHTML = slider_temp.value;

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

}

thermometer_socket.onopen = function (e) {
    const data = JSON.parse(e.data);
    document.querySelector('#msg_temp').innerHTML = (data.data)
    thermometer_socket.send(JSON.stringify({
        'data': this.value,
    }));

}