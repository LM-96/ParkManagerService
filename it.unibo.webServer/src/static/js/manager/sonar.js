var status_sonar = document.getElementById("status_sonar");

const sonar_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/sonar/'
);

sonar_socket.onmessage = function (e) {
    console.log("SONAR")
    console.log(e)

    const json_data = JSON.parse(e.data);
    console.log(json_data.data)

    status_sonar.innerHTML = json_data.data;
    
}
