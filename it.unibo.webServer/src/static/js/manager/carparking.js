const map_slot = {
    "1": {
        "x": "3",
        "y": "1"
    },
    "2": {
        "x": "3",
        "y": "2"
    },
    "3": {
        "x": "3",
        "y": "3"
    },
    "4": {
        "x": "2",
        "y": "1"
    },
    "5": {
        "x": "2",
        "y": "2"
    },
    "6": {
        "x": "2",
        "y": "3"
    },
}

const map_door = {
    "indoor": {
        "x": "6",
        "y": "0"
    },
    "outdoor": {
        "x": "6",
        "y": "4"
    }
}

const carparking_socket = new WebSocket(
    'ws://' +
    window.location.host +
    '/manager/carparking/'
);


carparking_socket.onmessage = function (e) {
    console.log(e.data)
    const json_data = JSON.parse(e.data)
    console.log(json_data)
    for (let key in json_data) {
        if(key in map_slot){
            colorParkingSlot(map_slot[key].x.concat(map_slot[key].y), json_data[key])
        }else if(key in map_door){
            
            colorDoor(key, map_door[key].x.concat(map_door[key].y), json_data[key])
        }
    }
    
}   

function colorDoor(name, coord, info){

    
    var door = document.getElementById(coord);
    if(info.state == "FREE"){
        door.style.backgroundColor = "#40FF33"
    }else if(info.state == "OCCUPIED"){
        door.style.backgroundColor = "#FF2525"
    }else if(info.state == "RESERVED"){
        door.style.backgroundColor = "#F2FF25"
    }

    document.getElementById("status_".concat(name)).innerHTML = info.state
    document.getElementById("user-".concat(coord)).innerHTML = info.user

    
}


function colorParkingSlot(coord, info){
    
    var parking_slot = document.getElementById(coord);
    if(info.state == "FREE"){
        parking_slot.style.backgroundColor = "#40FF33"
    }else if(info.state == "OCCUPIED"){
        parking_slot.style.backgroundColor = "#FF2525"
    }else if(info.state == "RESERVED"){
        parking_slot.style.backgroundColor = "#F2FF25"
    }

    document.getElementById("user-".concat(coord)).innerHTML = info.user
    
}



