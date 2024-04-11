import React, { useRef } from "react";
import PropTypes from 'prop-types';
import "./VertexButton.css";


function VertexButton(props) {

    const verticesRef = useRef([]);

    const [drawEdge,setDrawEdge]=React.useState(false);
    const [source,setSource]=React.useState(1);

    const handleMouseMove = (id, vertex) => (event) => {
        const rect = event.target.getBoundingClientRect();
        props.updateButtonCoordinates(id, rect.x+10, rect.y-120);
        //props.updateButtonCoordinates(id, vertex.x+10, vertex.y-120);
        if(drawEdge===true){
            if(id===props.source){
                setDrawEdge(false);
                return;

            }else{
               props.createEdge(source,id);
               setDrawEdge(false);
            }
        }else{
            setSource(id);
            setDrawEdge(true);
        }
    };
    const handleMouseDown=(id) =>(event) =>{


    }
    return (
        <div>
            {props.vertices ? props.vertices.map(vertex => {

                return (
                    <div 
                        key={vertex.id} 
                        ref={ref => verticesRef.current[vertex.id] = ref}
                        onClick={handleMouseMove(vertex.id, vertex.vertex)}
                        //onClick={handleMouseDown(vertex.id)}
                    >
                        {vertex.vertex}
                    </div>
                );
            }) : null}  
        </div>
    );
}


VertexButton.propTypes = {
    vertices: PropTypes.array,
    updateButtonCoordinates: PropTypes.func,
    drawEdge: PropTypes.bool,
    source: PropTypes.number,
    createEdge: PropTypes.func
}

export default VertexButton;

