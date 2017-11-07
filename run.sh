echo "running . setup.sh"

. setup.sh

echo "translating $1 ###########################"
java translator.Translator "test/$1.java"
echo "making $1 ###########################"
rm "*.o"
rm "Main"
make
echo "running $1 ###########################"
echo "C++ output ###########################"
./Main