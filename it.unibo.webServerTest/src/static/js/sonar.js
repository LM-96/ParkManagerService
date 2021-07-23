
var slider_sonar = document.getElementById("sonar");
var output_sonar = document.getElementById("value_sonar");
output_sonar.innerHTML = slider_sonar.value;

slider_sonar.oninput = function() {
    output_sonar.innerHTML = this.value;
    sonar_socket.send(JSON.stringify({
        'data': this.value,
    }));
};




const sonar_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/ws/sonar/'
);

sonar_socket.onmessage = function (e) {
    const data = JSON.parse(e.data);
    console.log(data)
    document.querySelector('#msg_sonar').innerHTML = (data.data)
    document.querySelector("#sonar").value = parseInt(data.data)
    document.querySelector("#value_sonar").innerHTML = (data.data)

}

sonar_socket.onconnect = function () {
    document.querySelector('#msg_sonar').innerHTML = (slider_sonar.value)
    sonar_socket.send(JSON.stringify({
        'data': slider_sonar.value,
    }));
}