# La forma natural de hacer el bind de los ficheros sql de setup de la bd 
# seria usar en el docker compose la variable de entorno WORKSPACE que pone jenkins.
# Esto asigna el path corecto a los bindings (si se examina el container con inspect)
# pero luego en tiempo de ejecucion no se localizan.
# Puede ser debido a que la localizacion del workspace ya es un bind en el slave
# y que esto causa un doble bind?

# La solucion sera este shell que modifica el docker compose:
# - Obtiene WORKSPACE_FOLDER_VAR como el ultimo componente del workspace
# - En el docker compose se hara el bind con la ruta absoluta al workspace general del slave,
#   anyadiendo WORKSPACE_FOLDER_VAR y luego la ruta de los ficheros.

# WORKSPACE="/ab/cd/efxxxxxxxxxx"

echo "Replacing workspace location in docker compose file docker-compose-jenkins.yaml"
WORKSPACE_FOLDER_VAR=`echo $WORKSPACE | awk -F/ '{print $NF}'`
echo "Workspace folder is $WORKSPACE_FOLDER_VAR"

sed  -i "s/WORKSPACE_FOLDER/${WORKSPACE_FOLDER_VAR}/" docker-compose-jenkins.yaml
