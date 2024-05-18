import React, { useRef, useEffect } from "react";
import PropTypes from 'prop-types';
import "./VertexButton.css";


function VertexButton(props) {
    const verticesRef = useRef([]);
    const [drawEdge,setDrawEdge]=React.useState(false);
    const [source,setSource]=React.useState(0);
    const [id, setId] = React.useState(0);

    const handleMouseMove = (id) => () => {
        setId(id);    
        createEdge(id);
        deleteEdge(id);
        deleteVertex(id);
    };

    useEffect(() => {
        const mouseMove = (event) => {
            if (id !== 0) {
                const rect = event.target.getBoundingClientRect();
                const v = document.getElementsByClassName('vertex');
                const a = Array.prototype.find.call(v, ver => (
                    rect.y <= ver.getBoundingClientRect().bottom &&
                    rect.y >= ver.getBoundingClientRect().top &&
                    rect.x <= ver.getBoundingClientRect().right &&
                    rect.x >= ver.getBoundingClientRect().left
                ));
                if (a) {
                    props.updateButtonCoordinates(id,
                        a.getBoundingClientRect().left + (a.getBoundingClientRect().right - a.getBoundingClientRect().left) / 2,
                        a.getBoundingClientRect().top + (a.getBoundingClientRect().top - a.getBoundingClientRect().bottom) / 2 - 55
                    );
                }
            }
        };

        const mouseUp = () => {
            setId(0);
        };

        window.addEventListener('mousemove', mouseMove);
        window.addEventListener('mouseup', mouseUp);

        return () => {
            window.removeEventListener('mousemove', mouseMove);
            window.removeEventListener('mouseup', mouseUp);
        };
    }, [id, props])


    function createEdge(id){ 
        if (props.edgeCreate === true){
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
        } 
    }

    function deleteEdge(id){
        if (props.edgeRemove === true){
            if(drawEdge===true){
                if(id===props.source){
                    setDrawEdge(false);
                    return;
                }else{
                    props.deleteEdge(source,id);
                    setDrawEdge(false);
                }
            }else{
                setSource(id);
                setDrawEdge(true);
            }
        }
    }
    
    function deleteVertex(id){
        if (props.vertexRemove === true){
            props.deleteVertex(id);
        }
    }

    useEffect(() => {    
        const handleKeyPress = (event) => {
          if (event.key === 'd'){
            const v = document.getElementsByClassName('vertex');
            const a = Array.prototype.find.call(v, ver => (
                props.coordinates[1] <= ver.getBoundingClientRect().bottom &&
                props.coordinates[1] >= ver.getBoundingClientRect().top &&
                props.coordinates[0] <= ver.getBoundingClientRect().right &&
                props.coordinates[0] >= ver.getBoundingClientRect().left
            ));
            if (a) {
                console.log(a.children[0].textContent);
                deleteVertexx(parseInt(a.children[0].textContent));
            }
          }
        };
    
        function deleteVertexx(id){
            props.deleteVertex(id);
        }

        window.addEventListener('keypress', handleKeyPress);
    
        return (() => {
          window.removeEventListener('keypress', handleKeyPress);
        })
      });

    return (
        <div>
            {props.vertices ? props.vertices.map(vertex => {
                return (
                    <div 
                        key={vertex.id} 
                        ref={ref => verticesRef.current[vertex.id] = ref}
                        onMouseDown={handleMouseMove(vertex.id)}
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
    coordinates: PropTypes.array,
    updateButtonCoordinates: PropTypes.func,
    deleteEdge: PropTypes.func,
    edgeRemove: PropTypes.bool,
    vertexRemove: PropTypes.bool,
    deleteVetrex: PropTypes.func,
    edgeCreate: PropTypes.bool,
    source: PropTypes.number,
    createEdge: PropTypes.func,
}

export default VertexButton;

